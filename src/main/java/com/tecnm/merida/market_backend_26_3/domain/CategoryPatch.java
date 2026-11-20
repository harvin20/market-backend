package com.tecnm.merida.market_backend_26_3.domain;

import jakarta.validation.constraints.Size;

public record CategoryPatch(@Size(min = 1) String category, Boolean active) {}
