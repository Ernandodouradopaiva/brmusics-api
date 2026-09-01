CREATE TABLE sso_handoff_ticket (
    id BIGSERIAL PRIMARY KEY,
    ticket_hash VARCHAR(255) NOT NULL,
    access_token TEXT NOT NULL,
    token_expires_in BIGINT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sso_handoff_ticket_hash ON sso_handoff_ticket (ticket_hash);
