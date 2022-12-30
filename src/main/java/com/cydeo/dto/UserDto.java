package com.cydeo.dto;
import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    Long id;

    @NotBlank(message = "First Name is a required field.")
    @Size(max = 50, min = 2, message = "First Name should be 2-50 characters long.")
    String firstname;

    @NotBlank(message = "Last Name is a required field.")
    @Size(max = 50, min = 2, message = "Last Name should be 2-50 characters long.")
    String lastname;

    @NotBlank (message = "A user with this email already exists. Please try with different email.")
    @Email(message = "Email is a required field.")
    String username;

    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$" // (ex: +1 (957) 463-7174)
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$", message = "Phone number is required field and may be in any valid phone number format.")
    String phone;

    @NotBlank (message = "Password is a required field.")
    @Pattern(regexp = "(?=.\\d)(?=.[a-z])(?=.*[A-Z]).{4,}")
    String password;

    @NotBlank (message = "Passwords should match.")
    @Pattern(regexp = "(?=.\\d)(?=.[a-z])(?=.*[A-Z]).{4,}")
    String confirmPassword;

    @NotBlank (message = "Please select a role")
    RoleDto role;

    @NotBlank (message = "Please select a customer")
    CompanyDto company;

    Boolean isOnlyAdmin;






}
