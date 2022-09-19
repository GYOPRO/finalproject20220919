package upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UploadController {

	@Autowired
	@Qualifier("uploadservice1")
	UploadService service;
	
	@GetMapping("/fileupload")
	public String uploadform() {
		return "upload/uploadform";
	}
	@PostMapping("/fileupload")
	String uploadprocess(@ModelAttribute("dto") UploadDTO dto) 
	throws IOException {
	 // dto 같은 이름 변수에 파라미터 매핑되어있다
	// file1, file2 의 이름과 내용 서버 c:\\upload 폴더에 저장 	
	// 파일내용을 복사하여 c:\\upload 폴더에 파일명 붙여넣기
	
		//폴더 없으면 수동 생성
		String savePath ="c:/upload/";
		
		MultipartFile mf1 = dto.getFile1();
		if(!mf1.isEmpty()) {
			String originalname1 = mf1.getOriginalFilename(); //a.txt
			String beforeext1 = originalname1.substring(0, originalname1.indexOf(".")); //a
			String ext1 = originalname1.substring(originalname1.indexOf(".")); // .txt
			String newname1 = beforeext1+"("+UUID.randomUUID().toString()+")"+ext1;
			File servefile1 = new File(savePath+newname1); // a(012334434).txt
			System.out.println(savePath+newname1);
			mf1.transferTo(servefile1);
			dto.setFilename1(newname1);//서버 저장이름 
		}
			
		MultipartFile mf2 = dto.getFile2();
		if(!mf2.isEmpty()) {
			String originalname2 = mf2.getOriginalFilename();
			String beforeext2 = originalname2.substring(0, originalname2.indexOf(".")); //a
			String ext2 = originalname2.substring(originalname2.indexOf(".")); // .txt
			String newname2 = beforeext2+"("+UUID.randomUUID().toString()+")"+ext2;
			File servefile2 = new File(savePath+newname2);	
			mf2.transferTo(servefile2);
			dto.setFilename2(newname2);//서버 저장이름

		}
		int result = service.register(dto);
		System.out.println(result);

		
		return "upload/uploadprocess";

	}
	//inputname url 매핑 메소드 
	@RequestMapping("/inputname")
	public String inputName() {
		return "upload/inputname";
	}
	///outputimage url 매핑 메소드
	@RequestMapping("/outputimage")
	public ModelAndView outputimage(String name) {
		// List<UploadDTO><-select * from uoload where name=입력한이름
		List<UploadDTO> list = service.get(name);//db 모든 정보 포함
		ArrayList<String> filenames = new ArrayList();//이미지파일 추출 저장 용도
		System.out.println(list.size());
		
		
		for(UploadDTO dto : list) {
			String [] file_split = dto.getFilename1().split("\\.");
			String ext = file_split[file_split.length-1]; //확장자
			System.out.println("ext="+ext);
			if(ext.equals("gif") ||  ext.equals("jfif") || ext.equals("jpg")) {
				filenames.add(dto.getFilename1());
			}
			String [] file_split2 = dto.getFilename2().split("\\.");
			String ext2 = file_split2[file_split2.length-1]; //확장자
			if(ext2.equals("gif") ||  ext2.equals("jfif") || ext2.equals("jpg")) {
				filenames.add(dto.getFilename2());
			}		
		}//for end
		
//		for(UploadDTO dto : list) {
//			String [] file_split1 = dto.getFilename1().split("\\.");
//			String ext1 = file_split1[file_split1.length-1]; //확장자
//			if(ext1.equals("gif") ||  ext1.equals("jfif") || ext1.equals("jpg")) {
//				filenames.add(dto.getFilename1());
//			}
//			String [] file_split2 = dto.getFilename2().split("\\.");
//			String ext2 = file_split2[file_split2.length-1]; //확장자
//			if(ext2.equals("gif") ||  ext2.equals("jfif") || ext2.equals("jpg")) {
//				filenames.add(dto.getFilename2());
//			}				
//		}
		
		//filenames 내부에는 현재이름 사용자 업로드한 파일 목록   gif, jfif, jpg 확장자 저장
		// gif, jfif, jpg 확장자 골라내자
		System.out.println(filenames.size());
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("filenames", filenames);
		mv.setViewName("upload/outputimage");
		return mv;
	}
	//service 추가 - dao 추가 - upload-mapping.xml 추가


}


/*  1. /inputname url 매핑 메소드 - 사용자이름 입력폼 화면(action=/outputimage)
 *  2. /outputimage url 매핑 메소드 - 입력 이름 받아서 사용자 업로드했던 파일(jpg이거나 jfif, gif 확장자) 파일만 가져와서 
 *  이미지 출력 <img  
 * 
 * */










