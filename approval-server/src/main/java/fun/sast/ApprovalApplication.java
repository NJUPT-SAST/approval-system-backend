package fun.sast;

import fun.sast.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
@MapperScan("fun.sast.mapper")
public class ApprovalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApprovalApplication.class, args);
        log.info("server started");
    }

    @Bean
    public CommandLineRunner generateTestToken(JwtUtil jwtUtil) {
        return args -> {
            String testUserCode = "admin";

            String token = jwtUtil.createJwt(testUserCode);

            System.out.println("Generated JWT Token for user '" + testUserCode + "': " + token);
        };
    }
}
