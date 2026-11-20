package com.tecnm.merida.market_backend_26_3.domain;

import jakarta.validation.constraints.*;

public record ProductPatch(@Size(min = 1) String name, @Positive Integer categoryId,
                           @PositiveOrZero Double price, @PositiveOrZero Integer stock,
                           Boolean active) {}
