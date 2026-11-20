-- The expiry sweep looks for held holds whose hold period ended. Without this index it read every hold. See ADR-0008.
CREATE INDEX seat_hold_held_expires_at_idx ON seat_hold (expires_at) WHERE status = 'HELD';
