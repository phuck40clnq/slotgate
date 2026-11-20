-- Students read their current holds each time they open their page. The unique index of V3 leads with
-- section_id, so a lookup by student read all of it: 2.8 ms at 100,000 holds, against 0.06 ms with this index.
CREATE INDEX seat_hold_active_by_student_idx ON seat_hold (student_id) WHERE status IN ('HELD', 'CONFIRMED');
