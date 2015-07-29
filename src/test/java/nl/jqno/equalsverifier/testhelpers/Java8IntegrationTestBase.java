/*
 * Copyright 2015 Jan Ouwens
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.jqno.equalsverifier.testhelpers;

import java.io.File;
import java.io.IOException;

import nl.jqno.equalsverifier.internal.ConditionalInstantiator;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

public class Java8IntegrationTestBase extends IntegrationTestBase {
    private ConditionalCompiler compiler;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException {
        File tempFileLocation = tempFolder.newFolder();
        compiler = new ConditionalCompiler(tempFileLocation);
    }

    @After
    public void tearDown() {
        compiler.close();
    }

    public Class<?> compile(String className, String code) {
        return compiler.compile(className, code);
    }

    public boolean isJava8Available() {
        return isTypeAvailable("java.util.Optional");
    }

    private boolean isTypeAvailable(String fullyQualifiedTypeName) {
        return new ConditionalInstantiator(fullyQualifiedTypeName).resolve() != null;
    }
}
