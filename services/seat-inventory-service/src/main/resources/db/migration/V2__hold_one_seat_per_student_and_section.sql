-- One hold per student and section makes repeated hold requests idempotent. See ADR-0006.
ALTER TABLE seat_hold
    ADD CONSTRAINT seat_hold_one_per_student_and_section UNIQUE (section_id, student_id);

-- The unique index leads with section_id, so it also serves every lookup by section.
DROP INDEX seat_hold_section_id_idx;
