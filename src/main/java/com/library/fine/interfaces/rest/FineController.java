package com.library.fine.interfaces.rest;

import com.library.fine.application.dto.PayFineCommand;
import com.library.fine.application.usecase.GetFineUseCase;
import com.library.fine.application.usecase.GetUserFinesUseCase;
import com.library.fine.application.usecase.PayFineUseCase;
import com.library.fine.interfaces.rest.dto.FineResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/fines")
public class FineController {

    private final PayFineUseCase payFineUseCase;
    private final GetFineUseCase getFineUseCase;
    private final GetUserFinesUseCase getUserFinesUseCase;

    public FineController(PayFineUseCase payFineUseCase, GetFineUseCase getFineUseCase,
                          GetUserFinesUseCase getUserFinesUseCase) {
        this.payFineUseCase = payFineUseCase;
        this.getFineUseCase = getFineUseCase;
        this.getUserFinesUseCase = getUserFinesUseCase;
    }

    @GetMapping("/{fineId}")
    public ResponseEntity<FineResponseDto> getFine(@PathVariable UUID fineId) {
        FineResponseDto response = FineResponseDto.from(getFineUseCase.execute(fineId));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{fineId}/pay")
    public ResponseEntity<FineResponseDto> payFine(@PathVariable UUID fineId) {
        FineResponseDto response = FineResponseDto.from(payFineUseCase.execute(new PayFineCommand(fineId)));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<FineResponseDto>> getUserFines(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "false") boolean onlyPending) {
        List<FineResponseDto> fines = getUserFinesUseCase.execute(userId, onlyPending)
                .stream().map(FineResponseDto::from).toList();
        return ResponseEntity.ok(fines);
    }
}
