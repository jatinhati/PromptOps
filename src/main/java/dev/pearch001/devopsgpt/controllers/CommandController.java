package dev.pearch001.devopsgpt.controllers;

import dev.pearch001.devopsgpt.model.CommandRequest;
import dev.pearch001.devopsgpt.model.CommandResponse;
import dev.pearch001.devopsgpt.service.CommandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/command")
public class CommandController {

    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    /**
     * Endpoint to generate a command from a plain-text task description.
     *
     * @param commandRequest The user's task.
     * @return A CommandResponse containing the generated command and explanation.
     */
    @PostMapping("/generate")
    public ResponseEntity<CommandResponse> generateCommand(@Valid @RequestBody CommandRequest commandRequest) {
        CommandResponse response = commandService.generateCommand(commandRequest.task());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to simulate the execution of a given command.
     *
     * @param payload A map containing the command to execute.
     * @return A string with fake log output.
     */
    @PostMapping("/simulate")
    public ResponseEntity<String> simulateCommand(@RequestBody Map<String, String> payload) {
        String command = payload.get("command");
        if (command == null || command.isBlank()) {
            return ResponseEntity.badRequest().body("'command' field is required.");
        }
        String simulationResult = commandService.simulateExecution(command);
        return ResponseEntity.ok(simulationResult);
    }
}
