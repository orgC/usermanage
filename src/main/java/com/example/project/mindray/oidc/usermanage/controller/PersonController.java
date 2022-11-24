package com.example.project.mindray.oidc.usermanage.controller;

import com.example.project.mindray.oidc.usermanage.dto.PersonDTO;
import com.example.project.mindray.oidc.usermanage.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import com.example.project.mindray.oidc.usermanage.domain.Person;
import com.example.project.mindray.oidc.usermanage.domain.Role;
import com.example.project.mindray.oidc.usermanage.domain.UserRole;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonRepository personRepository;
    private final RestTemplate restTemplate;

    @Value("${server.client-id}")
    private String client_id;
    @Value("${server.client-secret}")
    private String client_secret;

    @Value("${server.token-endpoint}")
    private String token_endpoint;

    @Value("${server.target-Service}")
    private String target_Service;

    public PersonController(PersonRepository personRepository, RestTemplate restTemplate) {
        this.personRepository = personRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public ResponseEntity<List<Person>> findAll() {
        List<Person> people = StreamSupport.stream(
                this.personRepository.findAll().spliterator(), false
        )
                .map(person -> {
                    List<UserRole> roomCreatedByPerson = restTemplate.exchange(
                            target_Service+"/userrole/authorId/" + person.getId(),
                            HttpMethod.GET, null, new ParameterizedTypeReference<List<UserRole>>() {
                            }
                    ).getBody();
                    person.setUserRole(roomCreatedByPerson);
                    return person;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(people);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        Person person = this.personRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person with id = " + id + " not found"
                ));
        List<UserRole> roomCreatedByPerson = restTemplate.exchange(
                target_Service+"/userrole/authorId/" + person.getId(),
                HttpMethod.GET, null, new ParameterizedTypeReference<List<UserRole>>() {
                }
        ).getBody();
        person.setUserRole(roomCreatedByPerson);
        return new ResponseEntity<Person>(person, HttpStatus.OK);
    }

    /**
     *
     * 创建用户，并授予默认角色
     * @param person new person
     * @return person with id. Person has default Role (USER).
     */
    @PostMapping({"/", "/sign-up/"})
    public ResponseEntity<Person> create(@RequestBody Person person, @RequestParam(value = "Authorization", defaultValue = "0") String authToken) {
        checkName(person.getName());
        checkPassword(person.getPassword());
        Person newPerson = this.personRepository.save(this.grantDefaultRoles(person));

        StreamSupport.stream(
                newPerson.getUserRole().spliterator(), false
                ).map(
                        userRole -> {
                            userRole.setAuthorId(newPerson.getId());
                            return  userRole;
                        }
                ).collect(Collectors.toList());

        // 登记授权记录
        // 构建post请求的 Body
        List<UserRole> body = newPerson.getUserRole();
        // 构建post请求的 Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getClientToken());

        // 构建post请求的携带数据
        HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

        restTemplate.exchange(
                target_Service+"/userrole/",
                HttpMethod.POST,
                requestEntity,
                ArrayList.class).getBody();

        return new ResponseEntity<Person>(newPerson, HttpStatus.CREATED);
    }


    @GetMapping("/token")
    public String token(){
        return this.getClientToken();
    }



    /**
     * 为用户提供默认授权，分配默认角色
     * @param person
     */
    private Person grantDefaultRoles(Person person){
        // 获取默认角色,并授权给新用户
        Role myRole = restTemplate.getForObject(target_Service+"/role/name/user", Role.class);
        UserRole userRole = new UserRole(myRole.getName(),0);
        person.getUserRole().add(userRole);
        person.setRoleId(myRole.getId());
        person.setPassword(person.getPassword());
        return person;
    }
    private void checkName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Person's name must not be empty");
        }
        if (personRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Person with name " + name + " already exists");
        }
    }
    private void checkPassword(String password) {
        if (password == null || password.length() < 5) {
            throw new IllegalArgumentException("Password must be more than 4 characters");
        }
    }

    /**
     * 获取客户端访问token access_token
     * @return
     */
    private String getClientToken(){
        // 获取证书的端点
        String tokenUrl = token_endpoint+"/protocol/openid-connect/token";

        // 构建post请求的 Body
        MultiValueMap bodys = new LinkedMultiValueMap();
        bodys.put("grant_type",new ArrayList(Arrays.asList("client_credentials")));
        bodys.put("client_id",new ArrayList(Arrays.asList(client_id)));
        bodys.put("client_secret",new ArrayList(Arrays.asList(client_secret)));


        // 构建post请求的 Header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        // 构建post请求的携带数据
        HttpEntity<?> requestEntity = new HttpEntity<>(bodys, headers);

        Map responseBody = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class).getBody();
        String accessToken = (String)responseBody.get("access_token");
        return accessToken;
    }
}
