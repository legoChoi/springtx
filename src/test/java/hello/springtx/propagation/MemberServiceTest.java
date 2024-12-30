package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    /**
     * memberService    @Transactional OFF
     * memberRepository @Transactional ON
     * logRepository    @Transactional ON
     */
    @Test
    void outerTxOff_success() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isPresent());
    }

    /**
     * 회원은 저장O, 로그는 저장X 데이터 정합성 문제 발생 가능
     * memberService    @Transactional OFF
     * memberRepository @Transactional ON
     * logRepository    @Transactional ON Exception
     */
    @Test
    void outerTxOff_fail() {
        // given
        String username = "로그예외_outerTxOff_fail";

        // when
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> memberService.joinV1(username))
                        .isInstanceOf(RuntimeException.class);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isEmpty());
    }

    /**
     * memberService    @Transactional ON
     * memberRepository @Transactional OFF
     * logRepository    @Transactional OFF
     */
    @Test
    void singleTx() {
        // given
        String username = "outerTxOff_success";

        // when
        memberService.joinV1(username);

        // then
        assertTrue(memberRepository.findByUsername(username).isPresent());
        assertTrue(logRepository.findByMessage(username).isPresent());
    }
}