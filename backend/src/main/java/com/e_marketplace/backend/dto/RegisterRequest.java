package com.e_marketplace.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO cho yêu cầu đăng ký người dùng
 */
public class RegisterRequest {
    
    @NotBlank(message = "Tên là bắt buộc")
    @Size(min = 2, max = 50, message = "Tên phải có từ 2 đến 50 ký tự")
    private String firstName;
    
    @NotBlank(message = "Họ là bắt buộc")
    @Size(min = 2, max = 50, message = "Họ phải có từ 2 đến 50 ký tự")
    private String lastName;
    
    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Email phải hợp lệ")
    private String email;
    
    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]", 
             message = "Mật khẩu phải chứa ít nhất một chữ hoa, một chữ thường, một số và một ký tự đặc biệt")
    private String password;
    
    @NotBlank(message = "Số điện thoại là bắt buộc")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại phải có từ 10 đến 15 chữ số")
    private String phoneNumber;
    
    // Constructors
    public RegisterRequest() {}
    
    public RegisterRequest(String firstName, String lastName, String email, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
    
    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }
}
