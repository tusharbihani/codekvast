/*
 * Copyright (c) 2015-2017 Crisp AB
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
package io.codekvast.warehouse.heroku;

/**
 * Service for handling Heroku provisioning, deprovisioning and plan changes.
 *
 * @author olle.hallin@crisp.se
 */
public interface HerokuService {
    /**
     * Provision Codekvast for one Heroku app.
     *
     * @param request The provisioning request sent by Heroku.
     * @return The response that Heroku will forward to the app developer.
     * @throws HerokuException If failed to satisfy the request.
     */
    HerokuProvisionResponse provision(HerokuProvisionRequest request) throws HerokuException;

    /**
     * Request to change plan.
     *
     * @param externalId The value of {@link HerokuProvisionResponse#id}.
     * @param request    The change plan request.
     * @throws HerokuException If failed to deprovision, try again later.
     */
    void changePlan(String externalId, HerokuChangePlanRequest request) throws HerokuException;

    /**
     * Deprovision Codekvast from one Heroku app.
     *
     * @param externalId The value of {@link HerokuProvisionResponse#id}.
     * @throws HerokuException If failed to deprovision, try again later.
     */
    void deprovision(String externalId) throws HerokuException;

    /**
     * @param externalId       The value of {@link HerokuProvisionResponse#id}
     * @param timestampSeconds The timestamp of the request, seconds since the epoch
     * @param token            The SSO token generated by Heroku
     * @param email            @throws HerokuSsoException
     */
    long singleSignOn(String externalId, long timestampSeconds, String token, String email) throws HerokuSsoException;
}