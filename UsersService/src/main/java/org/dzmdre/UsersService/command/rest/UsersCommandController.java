package org.dzmdre.UsersService.command.rest;

import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.dzmdre.UsersService.command.CreateUserCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/users")
public class UsersCommandController {
    private final CommandGateway commandGateway;

    @Autowired
    public UsersCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public String createProduct(@Valid @RequestBody CreateUserRestModel createUserRestModel) {
        CreateUserCommand createUserCommand = CreateUserCommand.builder()
                .firstName(createUserRestModel.getFirstName())
                .lastName(createUserRestModel.getLastName())
                .listPaymentDetails(createUserRestModel.getListPaymentDetails())
                .userId(UUID.randomUUID().toString())
                .build();
        return commandGateway.sendAndWait(createUserCommand);
    }
}
