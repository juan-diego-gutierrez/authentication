package co.com.pragma.model.user;

import co.com.pragma.model.role.Role;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

  private String name;
  private String lastName;
  private LocalDate birthDate;
  private String address;
  private String phone;
  private String email;
  private BigDecimal baseSalary;
  private Long roleId;
  private String password;
}
