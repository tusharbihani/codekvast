/*
 * Copyright (c) 2015-2017 Hallin Information Technology AB
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
package io.codekvast.warehouse.webapp.model.methods;

import lombok.Builder;
import lombok.ToString;
import lombok.Value;

import java.util.List;

/**
 * Response to {@link GetMethodsRequest1}.
 *
 * @author olle.hallin@crisp.se
 */
@Value
@Builder
@ToString(exclude = "methods")
public class GetMethodsResponse1 {
    /**
     * When was the request received? Millis since epoch.
     */
    private final Long timestamp;

    /**
     * What was the original request?
     */
    private final GetMethodsRequest1 request;

    /**
     * How long did it take to execute the request?
     */
    private final Long queryTimeMillis;

    /**
     * How many methods were found?
     */
    private final int numMethods;

    /**
     * The resulting methods.
     */
    private final List<MethodDescriptor1> methods;
}