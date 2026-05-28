package com.back.domain.member.service;

import com.back.domain.member.entity.Member;
import com.back.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public long count() {
        return memberRepository.count();
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Member getById(long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public Member join(String username, String password, String nickname) {
        if (memberRepository.existsByUsername(username)) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }
        if (memberRepository.existsByNickname(nickname)) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();

        return memberRepository.save(member);
    }

    @Transactional
    public void updateProfile(long memberId, String nickname, String rawPassword) {
        Member member = getById(memberId);

        if (!member.getNickname().equals(nickname) && memberRepository.existsByNickname(nickname)) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }
        member.changeNickname(nickname);

        if (rawPassword != null && !rawPassword.isBlank()) {
            member.changePassword(passwordEncoder.encode(rawPassword));
        }
    }
}
