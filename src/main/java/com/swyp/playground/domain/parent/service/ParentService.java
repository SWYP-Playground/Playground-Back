package com.swyp.playground.domain.parent.service;

import com.swyp.playground.common.S3.S3Service;
import com.swyp.playground.common.domain.TypeChange;
import com.swyp.playground.domain.child.domain.Child;
import com.swyp.playground.domain.child.dto.req.ChildUpdateReqDto;
import com.swyp.playground.domain.parent.domain.Parent;
import com.swyp.playground.domain.parent.dto.req.ParentCreateReqDto;
import com.swyp.playground.domain.parent.dto.req.ParentPasswordChangeReqDto;
import com.swyp.playground.domain.parent.dto.req.ParentUpdateReqDto;
import com.swyp.playground.domain.parent.dto.res.ParentCreateResDto;
import com.swyp.playground.domain.parent.repository.ParentRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ParentService {
    private final ParentRepository parentRepository;
    private final TypeChange typeChange;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private static final Logger logger = LoggerFactory.getLogger(ParentService.class);

    @Transactional
    public ParentCreateResDto signUp(ParentCreateReqDto request) {
        logger.info("logger check");
        if (isPhoneNumberDuplicate(request.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다: " + request.getPhoneNumber());
        }
    
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Parent parent = typeChange.parentCreateReqDtoToParent(request, encodedPassword);
        
        // 부모 엔티티를 먼저 저장하여 parentId 생성
        Parent savedParent = parentRepository.save(parent);

        // 자녀 정보 처리
        if (request.getChildren() != null) {
            request.getChildren().forEach(childDto -> {
                Child child = Child.builder()
                        .gender(childDto.getGender())
                        .birthDate(childDto.getBirthDate())
                        .age(LocalDate.now().getYear() - childDto.getBirthDate().getYear())
                        .parent(savedParent)  // 자녀에 부모 설정
                        .build();
                savedParent.addChild(child);
            });
        }

        // 프로필 이미지 업로드 처리
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            String fileUrl = uploadProfileImage(savedParent.getParentId(), request.getProfileImage());
            savedParent.setProfileImg(fileUrl);
        }

        parentRepository.save(savedParent);
    
        return typeChange.parentToParentCreateResDto(savedParent);
    }
    
    
    public boolean isPhoneNumberDuplicate(String phoneNumber) {
        return parentRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    public boolean isEmailDuplicate(String email) {
        return parentRepository.findByEmail(email).isPresent();
    }

    public boolean isNicknameDuplicate(String nickname) {
        return parentRepository.findByNickname(nickname).isPresent();
    }

    // 생년월일로 나이 계산 메서드 추가
    private int calculateAge(LocalDate birthDate) {
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    @Transactional
    public ParentCreateResDto getParentById(Long id){
        Parent parent = parentRepository.findParentWithChildren(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + id));
        List<Child> initializedChildren = parent.getChildren();
        initializedChildren.size(); // 초기화 강제 수행

        return typeChange.parentToParentCreateResDto(parent);
    }

    public List<ParentCreateResDto> getAllParents() {
        List<Parent> parents = parentRepository.findAll();
        return parents.stream()
                .map(typeChange::parentToParentCreateResDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public ParentCreateResDto updateParent(Long id, ParentUpdateReqDto request) {
        Parent parent = parentRepository.findParentWithChildren(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + id));

        updateParentInfo(parent, request);

        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            String fileUrl = uploadProfileImage(parent.getParentId(), request.getProfileImage());
            parent.setProfileImg(fileUrl);
        }

        if (request.getChildren() != null) {
            // 자녀 정보 업데이트
            for (ChildUpdateReqDto childDto : request.getChildren()) {
                if (childDto.getId() != null) {
                    // 기존 자녀 업데이트
                    Child existingChild = parent.getChildren().stream()
                            .filter(c -> c.getChildId().equals(childDto.getId()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("해당 자녀를 찾을 수 없습니다: " + childDto.getId()));
                    typeChange.updateChildFromDto(existingChild, childDto);
                } else {
                    // 새 자녀 추가
                    Child newChild = typeChange.childDtoToChild(childDto);
                    parent.addChild(newChild);
                }
            }
        }

        Parent updatedParent = parentRepository.save(parent);
        return typeChange.parentToParentCreateResDto(updatedParent);
    }

    private void updateParentInfo(Parent parent, ParentUpdateReqDto request) {
        if (request.getNickname() != null) parent.setNickname(request.getNickname());
        if (request.getAddress() != null) parent.setAddress(request.getAddress());
        if (request.getPhoneNumber() != null) parent.setPhoneNumber(request.getPhoneNumber());
        if (request.getIntroduce() != null) parent.setIntroduce(request.getIntroduce());
    }

    private void updateChildren(Parent parent, List<ChildUpdateReqDto> children) {
        // 기존 자녀 리스트 조회
        List<Child> existingChildren = parent.getChildren();

        for (ChildUpdateReqDto childDto : children) {
            if (childDto.getId() != null) {
                // 기존 자녀 수정
                Child existingChild = existingChildren.stream()
                        .filter(c -> c.getChildId().equals(childDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("해당 자녀를 찾을 수 없습니다: " + childDto.getId()));
                typeChange.updateChildFromDto(existingChild, childDto);
            } else {
                // 새로운 자녀 추가
                Child newChild = typeChange.childDtoToChild(childDto);
                parent.addChild(newChild);
            }
        }
    }

    public void deleteParentByEmail(String email) {
        System.out.println("서비스 호출 - 이메일: " + email);

        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다: " + email));
        parentRepository.delete(parent);
    }

    public void deleteParentById(Long id) {
        parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + id));
        parentRepository.deleteById(id);
    }
    public void resetPassword(String email, String newPassword) {
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다: " + email));

        String encodedPassword = passwordEncoder.encode(newPassword);
        parentRepository.updatePasswordByEmail(email, encodedPassword);
    }
    private String uploadProfileImage(Long parentId, MultipartFile file) {
        try {
            
            String fileName = "profiles/" + parentId + "_" + file.getOriginalFilename();
            logger.info("Saved file name: {}", fileName);
            return s3Service.uploadFile(fileName, file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("프로필 이미지 업로드 실패: " + e.getMessage(), e);
        }
    }

    public Parent getParentEntityById(Long id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다: " + id));
    }


    //email정보를 통해 Nickname 찾아오기
    public String getNicknameByEmail(String email) {
        Parent parent = parentRepository.findByEmail(email)
               .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다: " + email));
        return parent.getNickname();
    }
    public void changePassword(Long parentId, ParentPasswordChangeReqDto request) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getCurrentPassword(), parent.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("새로운 비밀번호가 일치하지 않습니다.");
        }

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("새로운 비밀번호가 현재 비밀번호와 같습니다.");
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        parent.setPassword(encodedNewPassword);
    }
}
