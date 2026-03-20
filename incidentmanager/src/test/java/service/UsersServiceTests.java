package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rodrigues.heric.incidentmanager.domain.UsersEntity;
import com.rodrigues.heric.incidentmanager.dto.CreateUsersRequest;
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
    @DisplayName("When the email is already in use it should throw Business Logic Exception (HTTP CONFLICT)")
    public void whenEmailAlreadyInUse_thenUsersServiceShouldReturnConflictStatus() {
        String email = "foo.bar@example.com";
        String name = "Foo Bar";
        String phone = "1234567890";

        CreateUsersRequest request = new CreateUsersRequest(name, email, phone);

        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(new UsersEntity()));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            usersService.createUser(request);
        });

        assertEquals("Email: " + email + " already in use.", exception.getMessage());

        verify(usersRepository).findByEmail(email);
        verify(usersMapper, never()).toEntity(any());
        verify(usersRepository, never()).save(any());
    }

}
