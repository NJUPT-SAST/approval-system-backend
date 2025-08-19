package fun.sast;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@MapperScan("fun.sast.mapper")
@ComponentScan(basePackages = {"fun.sast"})
public class ApprovalApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApprovalApplication.class, args);
        log.info("server started");
    }
}
