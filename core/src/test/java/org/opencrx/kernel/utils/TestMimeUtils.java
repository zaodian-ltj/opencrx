/*
 * ====================================================================
 * Project:     openCRX/Core, http://www.opencrx.org/
 * Description: TestMimeUtils
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
package org.opencrx.kernel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.internet.MimeMessage;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencrx.generic.AbstractTest;
import org.opencrx.kernel.backend.Accounts;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.lightweight.naming.LightweightInitialContextFactoryBuilder;

/**
 * TestMimeUtils
 */
public class TestMimeUtils extends AbstractTest {

    /**
     * @throws NamingException
     * @throws ServiceException
     */
    @BeforeEach
    public void initialize(
    ) throws NamingException, ServiceException {
        if(!NamingManager.hasInitialContextFactoryBuilder()) {
        	LightweightInitialContextFactoryBuilder.install(null);
        }
        entityManagerFactory = org.opencrx.kernel.utils.Utils.getPersistenceManagerFactory();
        pm = entityManagerFactory == null ? null : entityManagerFactory.getPersistenceManager();
    }

    @AfterEach
    public void tearDown(
    ){
    	if(this.pm != null) {
    		this.pm.close();
    	}
        this.pm = null;
    }
        
    /**
     * @throws ServiceException
     */
    @Test
    protected void testMimeUtils(
    ) throws ServiceException {
    	try {
    		org.opencrx.kernel.account1.jmi1.Segment accountSegment = Accounts.getInstance().getAccountSegment(pm, providerName, segmentName);	    		
    		File dir = new File("./etc/Outlook");
    		for(File file: dir.listFiles()) {
    			try {
    				String fileName = file.getPath();
    				if(fileName.endsWith(".msg")) {
    					fileName = fileName.substring(0, fileName.length() - 4);
				    	InputStream msgStream = new FileInputStream(fileName + ".msg");
				    	List<String> errors = new ArrayList<String>();
				    	MimeMessage mimeMessage = MimeUtils.mapMsgToMime(
				    		msgStream,
				    		accountSegment, // accountSegment
				    		Collections.<String,String>emptyMap(),
				    		true, // validateAddresses
				    		errors
				    	);
				    	if(mimeMessage != null) {
					    	FileOutputStream out = new FileOutputStream(fileName + ".eml");
					    	mimeMessage.writeTo(out);
					    	out.close();
				    	}
    				}
    			} catch(Exception e) {
    				new ServiceException(e).log();
    			}
    		}
    	} catch(Exception e) {
    		throw new ServiceException(e);
    	}
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
	protected static String providerName = "CRX";
	protected static String segmentName = "Standard";
		    
}
