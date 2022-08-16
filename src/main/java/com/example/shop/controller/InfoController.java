package com.example.shop.controller;

import com.example.shop.common.consts.ErrorConst;
import com.example.shop.domain.info.*;
import com.example.shop.domain.product.Product;
import com.example.shop.domain.product.ProductRepository;
import com.example.shop.dto.common.ResponseSavedIdDto;
import com.example.shop.dto.info.RequestNoticeListDto;
import com.example.shop.dto.info.RequestRegisterNoticeDto;
import com.example.shop.dto.info.RequestRegisterQnaDto;
import com.example.shop.dto.info.ResponseNoticeListDto;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class InfoController {

    private final NoticeRepository noticeRepository;

    private final QnaRepository qnaRepository;

    private final ProductRepository productRepository;

    @GetMapping("/notice")
    public ResponseEntity<?> getNotice(@Valid RequestNoticeListDto requestListDto) {
        PageRequest pageRequest = PageRequest.of(requestListDto.getPage(), requestListDto.getPageSize(), Sort.Direction.DESC, "notRegDate");
        Specification<Notice> specification = NoticeSpecification.getNoticeListSpecification(requestListDto);
        Page<Notice> noticePage = noticeRepository.findAll(specification, pageRequest);

        return ResponseEntity.ok(
                ResponseNoticeListDto.builder()
                        .totalCount(noticePage.getTotalElements())
                        .page(noticePage.getNumber())
                        .pageSize(noticePage.getSize())
                        .noticeItem(
                                noticePage.getContent().stream().map(
                                        notice -> ResponseNoticeListDto.NoticeListItem.builder()
                                                .notId(notice.getNotId())
                                                .notWriter(notice.getNotWriter())
                                                .notPw(notice.getNotPw())
                                                .notTitle(notice.getNotTitle())
                                                .notContent(notice.getNotContent())
                                                .notFile(notice.getNotFile())
                                                .notRegDate(notice.getNotRegDate())
                                                .build()
                                ).collect(Collectors.toList())
                        ).build()
        );
    }

    @PostMapping("/notice")
    public ResponseEntity<?> registerNotice(@RequestBody RequestRegisterNoticeDto noticeDto) {

        Notice notice = noticeRepository.save(
                Notice.builder()
                        .notWriter(noticeDto.getNotWriter())
                        .notPw(noticeDto.getNotPw())
                        .notTitle(noticeDto.getNotTitle())
                        .notContent(noticeDto.getNotContent())
                        .notFile(noticeDto.getNotFile())
                        .notRegDate(LocalDateTime.now())
                        .build()
        );

        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString());
        return ResponseEntity.created(selfLink).body(
                ResponseSavedIdDto.builder()
                        .savedId(notice.getNotId())
                        .build()
        );
    }

//    @PostMapping("/qna")
//    public ResponseEntity<?> registerQna(@RequestBody RequestRegisterQnaDto qnaDto) {
//
//        Product product = productRepository.findByProdId(qnaDto.getProdId())
//                .orElseThrow(() -> new NotFoundException(ErrorConst.NOT_FOUND_PRODUCT));
//
//        Qna qna = qnaRepository.save(
//                Qna.builder()
//                        .product(product)
//                        .qnaWriter(qnaDto.getQnaWriter())
//                        .qnaPw(qnaDto.getQnaPw())
//                        .qnaTitle(qnaDto.getQnaTitle())
//                        .qnaContent(qnaDto.getQnaContent())
//                        .qnaFile(qnaDto.getQnaFile())
//                        .qnaRegDate(LocalDateTime.now())
//                        .qnaSecret(qnaDto.getQnaSecret())
//                        .build()
//        );
//
//        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString());
//        return ResponseEntity.created(selfLink).body(
//                ResponseSavedIdDto.builder()
//                        .savedId(qna.getQnaId())
//                        .build()
//        );
//    }
}
