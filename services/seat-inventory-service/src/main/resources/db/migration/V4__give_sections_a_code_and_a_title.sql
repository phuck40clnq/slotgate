-- Students pick a section by what it is, so a section carries a code, unique among all sections, and a title.
ALTER TABLE section
    ADD COLUMN code  TEXT,
    ADD COLUMN title TEXT;

-- Sections opened before codes existed are simulated data. Their code is built from their id, which keeps it
-- unique, and cut to the 32 characters a code may have.
UPDATE section
SET code  = 'OLD-' || left(replace(id::text, '-', ''), 28),
    title = 'Simulated section';

-- The same limits as Section, so that the database refuses what the service could not read back.
ALTER TABLE section
    ALTER COLUMN code SET NOT NULL,
    ALTER COLUMN title SET NOT NULL,
    ADD CONSTRAINT section_code_is_unique UNIQUE (code),
    ADD CONSTRAINT section_code_has_1_to_32_characters CHECK (btrim(code) <> '' AND char_length(code) <= 32),
    ADD CONSTRAINT section_title_has_1_to_200_characters CHECK (btrim(title) <> '' AND char_length(title) <= 200);
