# i18n-sync-validator Agent Memory

## Message Properties File Structure

### Base File Location
- Base file: `/src/main/resources/messages/messages.properties`
- This is the source of truth for all message keys

### Language Files Present
The project supports 8 language variants (as of 2026-02-16):
- `messages_de.properties` (German)
- `messages_es.properties` (Spanish)
- `messages_fa.properties` (Persian/Farsi)
- `messages_ko.properties` (Korean)
- `messages_pt.properties` (Portuguese)
- `messages_ru.properties` (Russian)
- `messages_tr.properties` (Turkish)
- `messages_en.properties` (English variant, mostly empty)

Note: No French (fr) or Japanese (ja) files exist despite being mentioned in requests.

## Common Translation Patterns

### Visits Feature Keys
When "visits" keys are added, they typically include:
- `.title` - Feature heading
- `.showingDays` - Date range with {0}, {1}, {2} placeholders
- `.noVisits` - Empty state message
- `.date` - Column header
- `.description` - Column header

### Recurring English Fallback Issues
Some language files (pt, tr, fa, ko, ru) frequently have English fallback text for:
- `visit.date.required`
- `visit.date.future`
- `owner.duplicate`

These should be flagged and translated when found.

## Validation Test
- Test class: `I18nPropertiesSyncTest`
- Run command: `./mvnw test -Dtest=I18nPropertiesSyncTest`
- This test ensures all language files have matching keys with the base file

## Translation Quality Notes

### German (de)
- Use formal "Sie" form for user-facing messages
- "Besuche" for visits, "Datum" for date

### Spanish (es)
- Use "Próximas visitas" for upcoming visits
- "No hay" for "there are no"

### Portuguese (pt)
- Use "Próximas visitas" for upcoming visits
- "Nenhuma" for "no/none"

### Turkish (tr)
- Use "Yaklaşan" for upcoming
- "Planlanmış" for scheduled

### Persian (fa)
- Right-to-left text
- Use "آینده" for upcoming/future
- Use "ویزیت" for visit (borrowed word)

### Korean (ko)
- Use "예정된" for upcoming/scheduled
- Use "방문" for visit

### Russian (ru)
- Use "Предстоящие" for upcoming
- Use "визиты" for visits

## File Format Standards
- Encoding: UTF-8
- Format: `key=value` (no spaces around `=`)
- Preserve comments and blank lines
- Handle multi-line values with backslash continuation
