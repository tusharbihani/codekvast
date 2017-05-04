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
package io.codekvast.warehouse.file_import.impl;

import io.codekvast.javaagent.model.v1.CodeBasePublication;
import io.codekvast.javaagent.model.v1.CommonPublicationData;
import io.codekvast.warehouse.file_import.CodeBaseImporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * @author olle.hallin@crisp.se
 */
@Component
@Slf4j
public class CodeBaseImporterImpl implements CodeBaseImporter {

    private final ImportDAO importDAO;

    @Inject
    public CodeBaseImporterImpl(ImportDAO importDAO) {
        this.importDAO = importDAO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importPublication(CodeBasePublication publication) {
        log.debug("Importing {}", publication);

        CommonPublicationData commonData = publication.getCommonData();
        long appId = importDAO.importApplication(commonData);
        long jvmId = importDAO.importJvm(commonData);
        importDAO.importMethods(appId, jvmId, publication.getCommonData().getPublishedAtMillis(), publication.getEntries());
    }
}