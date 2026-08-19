CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY,
    email VARCHAR(254) NOT NULL UNIQUE,
    name VARCHAR(250) NOT NULL,

    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(50) NOT NULL UNIQUE,

    CONSTRAINT pk_categories PRIMARY KEY (category_id)
);

CREATE TABLE IF NOT EXISTS events (
    event_id BIGINT GENERATED ALWAYS AS IDENTITY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL REFERENCES users(user_id),
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    participant_limit INT NOT NULL DEFAULT 0,
    request_moderation BOOLEAN NOT NULL DEFAULT TRUE,
    title VARCHAR(120) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_at TIMESTAMP WITHOUT TIME ZONE,
    state VARCHAR(20) NOT NULL,

    CONSTRAINT pk_events PRIMARY KEY (event_id),
    CONSTRAINT fk_users FOREIGN KEY (initiator_id) REFERENCES users(user_id),
    CONSTRAINT fk_events_category FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT events_state_check CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED')),
    CONSTRAINT participant_limit_check CHECK (participant_limit >= 0)
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED ALWAYS AS IDENTITY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id BIGINT NOT NULL REFERENCES events(event_id),
    requester_id BIGINT NOT NULL REFERENCES users(user_id),
    status VARCHAR(20) NOT NULL,

    CONSTRAINT pk_requests PRIMARY KEY (request_id),
    CONSTRAINT uq_request UNIQUE (requester_id, event_id),
    CONSTRAINT requests_status_check CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS compilations (
    compilation_id BIGINT GENERATED ALWAYS AS IDENTITY,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    title VARCHAR(50) NOT NULL UNIQUE,

    CONSTRAINT pk_compilations PRIMARY KEY (compilation_id)
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT NOT NULL REFERENCES compilations(compilation_id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT pk_compilation_events PRIMARY KEY (compilation_id, event_id)
);

CREATE INDEX IF NOT EXISTS idx_events_category_id ON events(category_id);
CREATE INDEX IF NOT EXISTS idx_events_initiator_id ON events(initiator_id);
CREATE INDEX IF NOT EXISTS idx_events_event_date ON events(event_date);
CREATE INDEX IF NOT EXISTS idx_events_state ON events(state);
CREATE INDEX IF NOT EXISTS idx_requests_event_id ON requests(event_id);
CREATE INDEX IF NOT EXISTS idx_requests_requester_id ON requests(requester_id);
CREATE INDEX IF NOT EXISTS idx_compilations_pinned ON compilations(pinned);