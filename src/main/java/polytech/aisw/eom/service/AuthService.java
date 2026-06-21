package polytech.aisw.eom.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import polytech.aisw.eom.domain.AppUser;
import polytech.aisw.eom.domain.UserRole;
import polytech.aisw.eom.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser signup(String displayName, String username, String password, String passwordConfirm) {
        String cleanDisplayName = cleanRequired(displayName, "이름을 입력해주세요.");
        String cleanUsername = cleanRequired(username, "아이디를 입력해주세요.");
        String cleanPassword = cleanRequired(password, "비밀번호를 입력해주세요.");
        String cleanPasswordConfirm = cleanRequired(passwordConfirm, "비밀번호 확인을 입력해주세요.");

        if (cleanUsername.length() > 40) {
            throw new IllegalArgumentException("아이디는 40자 이하로 입력해주세요.");
        }
        if (cleanDisplayName.length() > 80) {
            throw new IllegalArgumentException("이름은 80자 이하로 입력해주세요.");
        }
        if (cleanPassword.length() < 4) {
            throw new IllegalArgumentException("비밀번호는 4자 이상 입력해주세요.");
        }
        if (!cleanPassword.equals(cleanPasswordConfirm)) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }
        if (userRepository.findByUsername(cleanUsername).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        return userRepository.save(new AppUser(
                cleanUsername,
                passwordEncoder.encode(cleanPassword),
                cleanDisplayName,
                null,
                UserRole.USER
        ));
    }

    private String cleanRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
