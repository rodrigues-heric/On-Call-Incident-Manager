package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.dto.CreateUsersRequest;
import com.rodrigues.heric.incidentmanager.dto.UsersDTO;
import com.rodrigues.heric.incidentmanager.exception.BusinessException;
import com.rodrigues.heric.incidentmanager.mapper.UsersMapper;
import com.rodrigues.heric.incidentmanager.repository.UsersRepository;
import com.rodrigues.heric.incidentmanager.service.UsersService;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTests {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private UsersMapper usersMapper;

    @InjectMocks
    private UsersService usersService;

    @Test
    @DisplayName("When the email is already in use it should throw Business Logic Exception")
    public void whenEmailAlreadyInUse_thenUsersServiceShouldThrowBusinessLogicException() {
        String email = "foo.bar@example.com";
        String name = "Foo Bar";
        String phone = "1234567890";

        CreateUsersRequest request = new CreateUsersRequest(name, email, phone);

        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(new UsersEntity()));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            this.usersService.createUser(request);
        });

        assertEquals("Email: " + email + " already in use.", exception.getMessage());

        verify(this.usersRepository).findByEmail(email);
        verify(this.usersMapper, never()).toEntity(any());
        verify(this.usersRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create a user successfully if the data are valid")
    public void shouldCreateUserSuccessfully() {
        String email = "foo.bar@example.com";
        String name = "Foo Bar";
        String phone = "1234567890";

        CreateUsersRequest request = new CreateUsersRequest(name, email, phone);
        UsersEntity userEntity = new UsersEntity();
        UsersEntity savedUser = new UsersEntity();
        UsersDTO expectedDTO = new UsersDTO(UUID.randomUUID(), name, email, phone);

        when(this.usersRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(this.usersMapper.toEntity(request)).thenReturn(userEntity);
        when(this.usersRepository.save(userEntity)).thenReturn(savedUser);
        when(this.usersMapper.toDTO(savedUser)).thenReturn(expectedDTO);

        UsersDTO result = this.usersService.createUser(request);

        assertNotNull(result);
        assertEquals(expectedDTO.email(), result.email());

        verify(this.usersRepository, times(1)).save(userEntity);
    }

}
