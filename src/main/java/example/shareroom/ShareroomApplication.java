package example.shareroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"shareroom.application","example.shareroom.dao",""})
public class ShareroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShareroomApplication.class, args);
	}

}
