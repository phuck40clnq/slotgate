-- A hold now ends: the student confirms it before expires_at, or it expires and its seat goes back. See ADR-0007.
ALTER TABLE seat_hold
    ADD COLUMN status     TEXT NOT NULL DEFAULT 'HELD',
    ADD COLUMN expires_at TIMESTAMPTZ;

-- Holds made before expiry existed expire at once, so that their seats go back to their sections.
UPDATE seat_hold SET expires_at = held_at;

ALTER TABLE seat_hold
    ALTER COLUMN status DROP DEFAULT,
    ALTER COLUMN expires_at SET NOT NULL,
    ADD CONSTRAINT seat_hold_status_is_known CHECK (status IN ('HELD', 'CONFIRMED', 'EXPIRED'));

-- Only a held or confirmed hold blocks another one, so a student can hold a section again once the hold expired.
-- Being partial, the index no longer serves lookups by section that do not filter on these statuses.
ALTER TABLE seat_hold DROP CONSTRAINT seat_hold_one_per_student_and_section;

CREATE UNIQUE INDEX seat_hold_one_active_per_student_and_section
    ON seat_hold (section_id, student_id)
    WHERE status IN ('HELD', 'CONFIRMED');
