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
package io.codekvast.login.heroku;

import io.codekvast.common.heroku.HerokuChangePlanRequest;
import io.codekvast.common.heroku.HerokuProvisionRequest;
import io.codekvast.common.heroku.HerokuProvisionResponse;
import io.codekvast.common.heroku.HerokuService;
import io.codekvast.login.bootstrap.CodekvastLoginSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * CRUD REST endpoints invoked by Heroku when doing 'heroku addons:create codekvast' etc.
 *
 * @author olle.hallin@crisp.se
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class HerokuResourcesController {

    private final CodekvastLoginSettings settings;
    private final HerokuService herokuService;

    @ExceptionHandler
    public ResponseEntity<String> onBadCredentialsException(BadCredentialsException e) {
        logger.warn("Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @RequestMapping(path = "/heroku/resources", method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<HerokuProvisionResponse> provision(@Valid @RequestBody HerokuProvisionRequest request,
                                                             @RequestHeader(AUTHORIZATION) String authorization) {
        logger.debug("request={}", request);

        validateBasicAuth(authorization);

        return ResponseEntity.ok(herokuService.provision(request));
    }

    @RequestMapping(path = "/heroku/resources/{id}", method = PUT)
    public ResponseEntity<String> changePlan(@PathVariable("id") String id,
                                             @Valid @RequestBody HerokuChangePlanRequest request,
                                             @RequestHeader(AUTHORIZATION) String auth) {
        logger.debug("id={}, request={}", id, request);

        validateBasicAuth(auth);

        herokuService.changePlan(id, request);

        return ResponseEntity.ok("{}");
    }

    @RequestMapping(path = "/heroku/resources/{id}", method = DELETE)
    public ResponseEntity<String> deprovision(@PathVariable("id") String id,
                                              @RequestHeader(AUTHORIZATION) String auth) {
        logger.debug("id={}", id);

        validateBasicAuth(auth);

        herokuService.deprovision(id);

        return ResponseEntity.ok("{}");
    }

    private void validateBasicAuth(String authentication) throws BadCredentialsException {
        logger.debug("authentication={}", authentication);

        // The password is defined in <rootDir>/deploy/vars/secrets.yml, and it has been pushed to Heroku by means
        // of <rootDir>/deploy/push-addon-manifest-to-heroku.sh

        String credentials = "codekvast:" + settings.getHerokuApiPassword();
        String expected = "Basic " + DatatypeConverter.printBase64Binary(credentials.getBytes());

        if (!authentication.equals(expected)) {
            throw new BadCredentialsException("Invalid credentials: " + authentication);
        }
    }
}
