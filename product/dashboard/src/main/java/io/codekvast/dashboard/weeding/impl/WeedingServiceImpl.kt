/*
 * Copyright (c) 2015-2019 Hallin Information Technology AB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.codekvast.dashboard.weeding.impl

import io.codekvast.dashboard.weeding.WeedingService
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

/**
 * Service that keeps the database tidy by removing child-less rows in methods, applications and environments.
 *
 * @author olle.hallin@crisp.se
 */
@Service
class WeedingServiceImpl @Inject constructor(private val jdbcTemplate: JdbcTemplate) : WeedingService {

    val logger = LoggerFactory.getLogger(this.javaClass)!!

    @Transactional(rollbackFor = [Exception::class])
    override fun performDataWeeding() {
        val startedAt = Instant.now()
        logger.debug("Performing data weeding")

        val invocationsBefore = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM invocations", Int::class.java)!!
        val deletedJvms = jdbcTemplate.update("DELETE FROM jvms WHERE garbage = TRUE ")

        if (deletedJvms > 0) {
            val invocationsAfter = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM invocations", Int::class.java)!!
            val deletedInvocations = invocationsBefore - invocationsAfter

            logger.debug("Deleted {} garbage jvms rows, which cascaded to approx. {} invocation rows", deletedJvms, deletedInvocations)

            val deletedMethods = jdbcTemplate.update("""
                DELETE m FROM methods AS m
                LEFT JOIN invocations AS i ON m.id = i.methodId
                WHERE i.methodId IS NULL""")

            val deletedApplications = jdbcTemplate.update("""
                DELETE a FROM applications AS a
                LEFT JOIN jvms AS j ON a.id = j.applicationId
                WHERE j.applicationId IS NULL""")

            val deletedEnvironments = jdbcTemplate.update("""
                DELETE e FROM environments AS e
                LEFT JOIN jvms AS j ON e.id = j.environmentId
                WHERE j.environmentId IS NULL""")
            logger.debug("Deleted {} unreferenced methods, {} empty applications and {} empty environments", deletedMethods, deletedApplications, deletedEnvironments)
        }

        val deletedAgentStates = jdbcTemplate.update("DELETE FROM agent_state WHERE garbage = TRUE ")

        if (deletedJvms + deletedAgentStates > 0) {
            logger.info("Data weeding: {} jvms rows and {} agent_state rows deleted in {}.", deletedJvms, deletedAgentStates, Duration.between(startedAt, Instant.now()))
        } else {
            logger.debug("Data weeding: Found nothing to delete")
        }
    }

}
