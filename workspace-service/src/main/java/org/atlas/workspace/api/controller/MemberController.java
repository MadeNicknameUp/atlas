package org.atlas.workspace.api.controller;

import lombok.RequiredArgsConstructor;
import org.atlas.workspace.api.dto.command.FindEntityQuery;
import org.atlas.workspace.api.dto.request.member.UpdateMemberRequest;
import org.atlas.workspace.api.dto.request.member.MemberFilter;
import org.atlas.workspace.api.dto.response.member.ThickGetMemberResponse;
import org.atlas.workspace.api.dto.response.member.ThinGetMemberResponse;
import org.atlas.workspace.api.service.MemberService;
import org.atlas.workspace.store.domain.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{workspace_id}/members")
    public ResponseEntity<List<ThinGetMemberResponse>> getMembers(
            @PathVariable("workspace_id") UUID workspaceId,
            @ModelAttribute MemberFilter filter,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, name = "page_size") Integer pageSize
    ) {

        UUID userId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        FindEntityQuery<Member> query = new FindEntityQuery<>(filter, page, pageSize);

        return ResponseEntity.ok(memberService.findMembersByWorkspaceId(
                        userId,
                        workspaceId,
                        query
                )
                .stream()
                .map(ThinGetMemberResponse::from)
                .toList()
        );
    }

    @GetMapping("/{workspace_id}/members/{member_id}")
    public ResponseEntity<ThickGetMemberResponse> getMember(
            @PathVariable(name = "workspace_id") UUID workspaceId,
            @PathVariable(name = "member_id") UUID memberId
    ) {

        UUID userId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        return ResponseEntity.ok(ThickGetMemberResponse.from(memberService.findById(
                        userId,
                        workspaceId,
                        memberId))
        );
    }

    @PatchMapping("/{workspace_id}/members/{member_id}")
    public ResponseEntity<ThickGetMemberResponse> updateMember(
            @PathVariable(name = "workspace_id") UUID workspaceId,
            @PathVariable(name = "memberId") UUID memberId,
            @RequestBody UpdateMemberRequest request
    ) {
        UUID userId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        return ResponseEntity.ok(ThickGetMemberResponse.from(memberService.updateMember(
                userId,
                workspaceId,
                memberId,
                request.toCommand()
        )));
    }

    @PostMapping("/{workspace_id}/members/{member_id}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable(name = "workspace_id") UUID workspaceId,
            @PathVariable(name = "memberId") UUID memberId
    ) {
        UUID userId = UUID.fromString("3adc4dd4-b5c0-4345-bf83-c44ef92188b9");

        memberService.expel(userId, workspaceId, memberId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}
