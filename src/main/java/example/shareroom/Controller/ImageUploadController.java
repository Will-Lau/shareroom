package example.shareroom.Controller;


import example.shareroom.UsefulUtils.Method;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "图片上传",tags="Image")
@RestController
@RequestMapping("/image")
public class ImageUploadController {

    @PostMapping("/upload")
    @ApiOperation(value = "上传图片", notes = "上传图片得到url", tags = "Image")
    public String upload(@RequestParam("file") MultipartFile file){
        return Method.uploadPic(file);
    }
}
