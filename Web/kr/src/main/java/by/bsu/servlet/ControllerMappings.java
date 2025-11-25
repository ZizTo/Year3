/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2016, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package by.bsu.servlet;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.web.IWebRequest;

import by.bsu.contr.*;


public class ControllerMappings {
 

    private static Map<String, IController> controllersByURL;


    static {
        controllersByURL = new HashMap<String, IController>();
        controllersByURL.put("/", new HomeController());
        controllersByURL.put("/welcome/students/list", new StudentController());
        controllersByURL.put("/welcome/subject/list", new CourseController());
       
        controllersByURL.put("/welcome/students/projections", new StudentProjectionsController());
        controllersByURL.put("/welcome/students/grodno", new StudentsGrodnoController());
        controllersByURL.put("/welcome/students/molodechno-active", new StudentsMolodechnoController());
        controllersByURL.put("/welcome/students/geometry", new StudentsGeometryController());
    }
    

    
    public static IController resolveControllerForRequest(final IWebRequest request) {
        final String path = request.getPathWithinApplication();
        System.out.println(path);
        return controllersByURL.get(path);
    }

    private ControllerMappings() {
        super();
    }


}
