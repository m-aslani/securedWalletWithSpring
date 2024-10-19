package com.example.securedwalletwithspring.controllerTests;

import com.example.securedwalletwithspring.controller.UserController;
import com.example.securedwalletwithspring.dto.EditedUserDto;
import com.example.securedwalletwithspring.dto.UserLoginDto;
import com.example.securedwalletwithspring.dto.UserRegistrationDto;
import com.example.securedwalletwithspring.entity.User;
import com.example.securedwalletwithspring.exception.UserNotFoundException;
import com.example.securedwalletwithspring.service.CustomUserDetailsService;
import com.example.securedwalletwithspring.service.JwtService;
import com.example.securedwalletwithspring.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        token = "Bearer " + generateMockToken();
    }

    private String generateMockToken() {
        return "jwtToken";
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setNationalId("1234567891");
        userRegistrationDto.setFirstname("Masoumeh");
        userRegistrationDto.setLastname("Aslani");
        userRegistrationDto.setEmail("masoumeh@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setPhoneNumber("09127270451");
        userRegistrationDto.setBirthDate("1/14/2001");
        userRegistrationDto.setGender("female");
        userRegistrationDto.setMilitaryStatus(false);
        userRegistrationDto.setInitialAmount(50);

        User user = new User();
        user.setNationalId("1234567891");

        when(userService.getUserByNationalId("1234567891")).thenReturn(Optional.empty());
        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(user);

        mockMvc.perform(post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
    }

    @Test
    public void testRegisterUserFailure() throws Exception {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setNationalId("1234567891");
        userRegistrationDto.setFirstname("Masoumeh");
        userRegistrationDto.setLastname("Aslani");
        userRegistrationDto.setEmail("masoumeh@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setPhoneNumber("09127270451");
        userRegistrationDto.setBirthDate("1/14/2001");
        userRegistrationDto.setGender("female");
        userRegistrationDto.setMilitaryStatus(false);
        userRegistrationDto.setInitialAmount(50);

        User user = new User();
        user.setNationalId("1234567891");

        when(userService.getUserByNationalId("1234567891")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/users/register")
        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userRegistrationDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("User with NationalId 1234567891 already exists"));
    }

    @Test
    public void testLoginSuccess()throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setNationalId("1234567891");
        userLoginDto.setPassword("password");

        User user = new User();
        user.setNationalId("1234567891");
        when(userService.getUserByNationalId("1234567891")).thenReturn(Optional.of(user));

        when(userService.loginUser(userLoginDto)).thenReturn("jwtToken");

        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userLoginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("jwtToken"));

    }

    @Test
    public void testLoginFailure() throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setNationalId("1234567891");
        userLoginDto.setPassword("password");
        User user = new User();
        user.setNationalId("1234567891");
        when(userService.getUserByNationalId("1234567891")).thenReturn(Optional.empty());

        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userLoginDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User with NationalId 1234567891 not found"));
    }

    @Test
    public void testFieldValidation() throws Exception {
        UserRegistrationDto userRegistrationDto = getUserRegistrationDto();

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
//                .andExpect(jsonPath("$.nationalId").value("Iranian national ID must include 10 digits!"));
//                .andExpect(jsonPath("$[0].firstname").value("user first name can NOT be Empty!"));
//                .andExpect(jsonPath("$.lastname").value("user last name can NOT be Empty!"))
//                .andExpect(jsonPath("$.password").value("user password can NOT be Empty!"))
//                .andExpect(jsonPath("$.email").value("E-mail is not valid!"))
//                .andExpect(jsonPath("$.phoneNumber").value("phone number is not valid!"))
//                .andExpect(jsonPath("$.birthDate").value("user birth date can NOT be Empty!"))
//                .andExpect(jsonPath("$.gender").value("user gender can NOT be Empty!"))
//                .andExpect(jsonPath("$.initialAmount").value("Initial Amount can Not be null!"));

    }

    private static UserRegistrationDto getUserRegistrationDto() {
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setNationalId("1234567891");
        userRegistrationDto.setFirstname("");
        userRegistrationDto.setLastname("Aslani");
        userRegistrationDto.setEmail("masoumeh@gmail.com");
        userRegistrationDto.setPassword("password");
        userRegistrationDto.setPhoneNumber("09127270451");
        userRegistrationDto.setBirthDate("1/14/2001");
        userRegistrationDto.setGender("female");
        userRegistrationDto.setMilitaryStatus(false);
        userRegistrationDto.setInitialAmount(50);
        return userRegistrationDto;
    }

    @Test
    public void testLoginFieldValidation()throws Exception {
        UserLoginDto userLoginDto = new UserLoginDto();
        userLoginDto.setNationalId("");
        userLoginDto.setPassword("password");

        User user = new User();
        user.setNationalId("1234567891");
        when(userService.getUserByNationalId("1234567891")).thenReturn(Optional.of(user));

        when(userService.loginUser(userLoginDto)).thenReturn("jwtToken");

        mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userLoginDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
//                .andExpect(jsonPath("$.nationalId").value("user national ID can NOT be Empty!"));

    }

    @Test
    public void testGetUser() throws Exception {
        EditedUserDto editedUserDto = new EditedUserDto();
        editedUserDto.setNationalId("1234567891");

        User user = new User();
        user.setNationalId("1234567891");
        when(userService.getUserByNationalId("1234567891")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/user")
                .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editedUserDto))
        ).andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser() throws Exception {
        EditedUserDto editedUserDto = new EditedUserDto();
        editedUserDto.setNationalId("1234567891");
        editedUserDto.setPhoneNumber("09366524125");

        User user = new User();
        user.setNationalId("1234567891");
        user.setPhoneNumber("09366524111");
        when(userService.getUserByNationalId("1234567891")).thenReturn(Optional.of(user));
        when(userService.updateUserPhoneNumber(editedUserDto , user)).thenReturn(editedUserDto.getPhoneNumber());

        mockMvc.perform(put("/update/phoneNumber")
                .header("Authorization",token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(editedUserDto))
        ).andExpect(status().isOk());
    }
}
