package com.postype.sns.domain.member.api;

import com.postype.sns.domain.member.dto.MemberDto;
import com.postype.sns.domain.member.dto.request.MemberLoginRequest;
import com.postype.sns.domain.member.dto.request.MemberRegisterRequest;
import com.postype.sns.domain.member.dto.response.AlarmResponse;
import com.postype.sns.domain.member.dto.response.MemberLoginResponse;
import com.postype.sns.domain.member.dto.response.MemberRegisterResponse;
import com.postype.sns.global.common.Response;
import com.postype.sns.domain.member.application.AlarmService;
import com.postype.sns.domain.member.application.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	private final AlarmService alarmService;

	@Operation(summary = "회원가입", description = "memberId, memberName, password, email로 신규 가입을 진행합니다")
	@PostMapping("/register")
	public Response<MemberRegisterResponse> register(@Valid @RequestBody MemberRegisterRequest request){
		return Response.success(
				MemberRegisterResponse.fromMemberDto(memberService.register(request)));
	}
	@Operation(summary = "로그인", description = "memberId와 password로 login 후 authorize를 위한 token 값을 받습니다")
	@PostMapping("/login")
	public Response<MemberLoginResponse> login(@Valid @RequestBody MemberLoginRequest request){
		String token = memberService.login(request);
		return Response.success(new MemberLoginResponse(token)); //dto <-> response 변환이 아닌 token값 반환
	}
	//TODO :: Alarm API description 추가
	@GetMapping("/alarm")
	public Response<Page<AlarmResponse>> getAlarm(@ApiIgnore @AuthenticationPrincipal MemberDto memberDto, Pageable pageable){
		return Response.success(
				memberService.getAlarmList(memberDto, pageable)
						.map(AlarmResponse::fromDto));
	}
	@GetMapping("/alarm/subscribe")
	public SseEmitter subscribe(@ApiIgnore @AuthenticationPrincipal MemberDto memberDto){
		return alarmService.connectAlarm(memberDto.getId());
	}

}
