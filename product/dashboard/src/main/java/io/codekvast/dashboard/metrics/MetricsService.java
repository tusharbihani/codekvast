/*
 * Copyright (c) 2015-2018 Hallin Information Technology AB
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
package io.codekvast.dashboard.metrics;

/**
 * Wrapper for all things related to metrics collection.
 *
 * @author olle.hallin@crisp.se
 */
public interface MetricsService {

    enum PublicationKind {CODEBASE, INVOCATIONS}

    /**
     * Updates the gauge for the number of queued publications.
     *
     * @param queueLength The queue length.
     */
    void gaugePublicationQueueLength(int queueLength);

    /**
     * Count the fact that a publication was rejected.
     */
    void countRejectedPublication();

    /**
     * Count the fact that a publication was imported.
     *
     * @param kind   The kind of publication.
     * @param format One of "v1", "v2".
     */
    void countImportedPublication(PublicationKind kind, String format);

    /**
     * Gauges the size of an imported publication.
     *
     * @param kind                The kind of publication.
     * @param customerId          The customer ID.
     * @param customerEnvironment The customer's environment (fetched from the publication header).
     * @param size                The size of the publication.
     */
    void gaugePublicationSize(PublicationKind kind, long customerId, String customerEnvironment, int size);
}