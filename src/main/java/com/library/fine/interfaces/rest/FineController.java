package com.library.fine.interfaces.rest;

import com.library.fine.application.dto.PayFineCommand;
import com.library.fine.application.usecase.GetFineUseCase;
import com.library.fine.application.usecase.GetUserFinesUseCase;
import com.library.fine.application.usecase.PayFineUseCase;
import com.library.fine.interfaces.rest.dto.FineResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
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
@Tag(name = "Fines", description = "Operaciones para consultar y pagar multas")
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

    @Operation(summary = "Obtener multa por id", description = "Retorna el detalle completo de una multa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Multa encontrada",
                    content = @Content(schema = @Schema(implementation = FineResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Multa no existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{fineId}")
    public ResponseEntity<FineResponseDto> getFine(
            @Parameter(description = "Id de la multa", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID fineId) {
        FineResponseDto response = FineResponseDto.from(getFineUseCase.execute(fineId));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Pagar multa", description = "Marca una multa pendiente como pagada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Multa pagada correctamente",
                    content = @Content(schema = @Schema(implementation = FineResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Multa no existe",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "La multa ya estaba pagada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/{fineId}/pay")
    public ResponseEntity<FineResponseDto> payFine(
            @Parameter(description = "Id de la multa a pagar", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID fineId) {
        FineResponseDto response = FineResponseDto.from(payFineUseCase.execute(new PayFineCommand(fineId)));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar multas por usuario",
            description = "Retorna multas de un usuario y permite filtrar solo pendientes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de multas",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FineResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public ResponseEntity<List<FineResponseDto>> getUserFines(
            @Parameter(description = "Id del usuario", example = "123e4567-e89b-12d3-a456-426614174000")
            @RequestParam UUID userId,
            @Parameter(description = "Si es true, retorna solo multas pendientes", example = "false")
            @RequestParam(defaultValue = "false") boolean onlyPending) {
        List<FineResponseDto> fines = getUserFinesUseCase.execute(userId, onlyPending)
                .stream().map(FineResponseDto::from).toList();
        return ResponseEntity.ok(fines);
    }
}
