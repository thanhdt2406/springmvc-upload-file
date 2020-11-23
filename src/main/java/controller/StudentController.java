package controller;

import dao.student.IStudentDAO;
import model.Student;
import model.StudentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private Environment environment;

    @Autowired
    private IStudentDAO studentDAO;

    @GetMapping()
    public ModelAndView index(){

        ModelAndView mav = new ModelAndView("student/list");
        mav.addObject("list", studentDAO.findAll());
        return mav;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editForm(@PathVariable Long id){
        ModelAndView mav = new ModelAndView("/student/edit");
        mav.addObject("student", studentDAO.findById(id));
        return mav;
    }

    @PostMapping("/edit/{id}")
    public ModelAndView editStudent(@ModelAttribute Student student){
        ModelAndView modelAndView = new ModelAndView("/student/edit");
        modelAndView.addObject("student", student);
        studentDAO.update(student);
        modelAndView.addObject("mess", "done edit");
        return modelAndView;
    }

    @GetMapping("/create")
    public ModelAndView showFormCreate(){
        ModelAndView modelAndView = new ModelAndView("/student/create");
        modelAndView.addObject("student", new StudentForm());
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView createStudent(@ModelAttribute StudentForm studentForm){
        //1 gan student nhung thuoc tinh cua studentForm
        Student student = new Student(studentForm.getName(), studentForm.getAddress());
        MultipartFile file = studentForm.getImage();
        String image = file.getOriginalFilename();
        student.setImage(image);
        String fileUpload = environment.getProperty("file_upload").toString();
        try {
            FileCopyUtils.copy(file.getBytes(), new File(fileUpload + image));
        } catch (IOException e) {
            e.printStackTrace();
        }

        studentDAO.save(student);
        return new ModelAndView("/student/create", "student", new StudentForm());

    }
}
