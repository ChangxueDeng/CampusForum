package org.campusforum.backend.entity.vo.request;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ResetConfirmVO {
    @Email
    private String email;
    @Length(min = 6, max = 6)
    private String code;
}
