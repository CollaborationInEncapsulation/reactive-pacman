package org.coinen.reactive.pacman.controller.grpc;

import com.google.protobuf.Empty;
import io.micrometer.core.instrument.MeterRegistry;
import io.rsocket.rpc.metrics.Metrics;
import org.coinen.pacman.Extra;
import org.coinen.pacman.ReactorExtrasServiceGrpc;
import org.coinen.reactive.pacman.controller.grpc.config.UUIDHolder;
import org.coinen.reactive.pacman.service.ExtrasService;
import org.lognet.springboot.grpc.GRpcService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import org.springframework.beans.factory.annotation.Qualifier;

@GRpcService
public class GrpcExtrasController extends ReactorExtrasServiceGrpc.ExtrasServiceImplBase {
    final ExtrasService extrasService;
    final MeterRegistry registry;

    public GrpcExtrasController(ExtrasService service,
        @Qualifier("grpc") MeterRegistry registry) {
        extrasService = service;
        this.registry = registry;
    }

    @Override
    public Flux<Extra> extras(Mono<Empty> request) {
        return extrasService.extras()
                            .onBackpressureDrop()
                            .transform(Metrics.<Extra>timed(registry, "grpc.server", "service", org.coinen.pacman.ExtrasService.SERVICE, "method", org.coinen.pacman.ExtrasService.METHOD_EXTRAS))
                            .subscriberContext(Context.of("uuid", UUIDHolder.get()));
    }
}
