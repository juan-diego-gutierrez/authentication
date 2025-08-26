package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.model.user.User;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  User toUser(UserDTO userDTO);

  UserDTO toUserDTO(User user);

  List<UserDTO> toResponseList(List<User> users);
}
