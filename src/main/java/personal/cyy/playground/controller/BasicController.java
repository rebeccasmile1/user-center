/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package personal.cyy.playground.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import personal.cyy.playground.User;
import personal.cyy.playground.common.annotation.SkipLogging;
import personal.cyy.playground.dao.AuthorDAO;
import personal.cyy.playground.domain.entity.AuthorEntity;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author <a href="mailto:chenxilzx1@gmail.com">theonefx</a>
 */
@Controller
public class BasicController {

    @Resource
    private AuthorDAO authorDAO;

//    // http://127.0.0.1:8080/hello?name=lisi
//    @GetMapping("/hello")
//    @ResponseBody
//    public String hello(@RequestParam(name = "name", defaultValue = "unknown user") String name) {
//        return "Hello " + name;
//    }
//
//    // http://127.0.0.1:8080/user
//    @GetMapping("/user")
//    @ResponseBody
//    public User user() {
//        User user = new User();
//        user.setName("theonefx");
//        user.setAge(666);
//        return user;
//    }
//
//    // http://127.0.0.1:8080/save_user?name=newName&age=11
//    @PostMapping("/save_user")
//    @ResponseBody
//    public String saveUser(User u) {
//        return "user will save: name=" + u.getName() + ", age=" + u.getAge();
//    }
//
//    // http://127.0.0.1:8080/html
//    @GetMapping("/html")
//    public String html() {
//        return "index.html";
//    }
//
//    @ModelAttribute
//    public void parseUser(@RequestParam(name = "name", defaultValue = "unknown user") String name
//            , @RequestParam(name = "age", defaultValue = "12") Integer age, User user) {
//        user.setName("zhangsan");
//        user.setAge(18);
//    }

    // http://127.0.0.1:8080/get/author
    @GetMapping("/get/author")
    @ResponseBody
    public AuthorEntity getAuthor(@RequestParam(name = "id", required = true) Integer id) {
        return authorDAO.getAuthor(id);
    }

    // http://127.0.0.1:8080/get/all_authors
    @SkipLogging
    @GetMapping("/get/all_authors")
    @ResponseBody
    public List<AuthorEntity> getAuthors(@RequestParam(name = "ids", required = true) List<Integer> ids) {
        return authorDAO.getAllAuthors(ids);
    }

}
