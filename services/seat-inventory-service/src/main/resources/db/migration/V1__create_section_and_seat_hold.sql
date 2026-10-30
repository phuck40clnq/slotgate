CREATE TABLE section (
    id             UUID PRIMARY KEY,
    capacity       INTEGER NOT NULL,
    reserved_seats INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT section_capacity_is_positive CHECK (capacity > 0),
    CONSTRAINT section_reserved_seats_within_capacity CHECK (reserved_seats BETWEEN 0 AND capacity)
);

CREATE TABLE seat_hold (
    id         UUID PRIMARY KEY,
    section_id UUID NOT NULL REFERENCES section (id),
    student_id UUID NOT NULL,
    held_at    TIMESTAMPTZ NOT NULL
);

CREATE INDEX seat_hold_section_id_idx ON seat_hold (section_id);
