/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: XmlDocToText
 * Owner:       the original authors.
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 * 
 * * Neither the name of the openCRX team nor the names of the contributors
 * to openCRX may be used to endorse or promote products derived
 * from this software without specific prior written permission
 * 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * ------------------
 * 
 * This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/).
 * 
 * This product includes software developed by contributors to
 * openMDX (http://www.openmdx.org/)
 */
package org.opencrx.kernel.text;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.loading.Classes;

/**
 * Re-factored from 
 * http://svn.apache.org/viewvc/poi/trunk/src/scratchpad/src/org/apache/poi/hwpf/extractor/WordExtractor.java?view=log
 */
public class XmlDocToText {

    /**
     * Gets the text from an XML document.
     * 
     * @param in The InputStream representing the XML document.
     */
    public Reader parse(
        InputStream in
    ) throws ServiceException {
        StringBuilder text = new StringBuilder();
		// POI 4
		try {
    		Class<?> extractorFactoryClass = Classes.getApplicationClass("org.apache.poi.ooxml.extractor.ExtractorFactory");
    		Class<?> packageHelperClass = Classes.getApplicationClass("org.apache.poi.ooxml.util.PackageHelper");
    		Method createExtractorMethod = extractorFactoryClass.getMethod("createExtractor", OPCPackage.class);
    		Method openMethod = packageHelperClass.getMethod("open", InputStream.class);
    		Object extractor = createExtractorMethod.invoke(
    			null,
    			openMethod.invoke(null, in)
    		);
    		Method getTextMethod = extractor.getClass().getMethod("getText");
    		text.append(getTextMethod.invoke(extractor));    			
    	} catch(Exception e) {
    		// POI 3
    		try {
        		Class<?> extractorFactoryClass = Classes.getApplicationClass("org.apache.poi.extractor.ExtractorFactory");
        		Class<?> packageHelperClass = Classes.getApplicationClass("org.apache.poi.util.PackageHelper");
        		Method createExtractorMethod = extractorFactoryClass.getMethod("createExtractor", OPCPackage.class);
        		Method openMethod = packageHelperClass.getMethod("open", InputStream.class);
        		Object extractor = createExtractorMethod.invoke(
        			null,
        			openMethod.invoke(null, in)
        		);
        		Method getTextMethod = extractor.getClass().getMethod("getText");
        		text.append(getTextMethod.invoke(extractor));
    			
    		} catch(Exception e1) {
    			throw new ServiceException(e1);
    		}
    	}
        return new StringReader(text.toString());
    }

}
