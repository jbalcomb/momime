# Test data paths fail on Linux/macOS

Patch: [`0001-test-data-paths.patch`](0001-test-data-paths.patch)

This changes test code only.

`ServerTestData` and `ClientTestData` find the XML databases by resolving `../` against a file, e.g.
`new File (".../target/classes/MoMIMEServerVersion.properties", "../../../src/...")`.
Windows normalizes `file.properties/..` without complaint. Linux and macOS don't, because the path treats a file as a
directory. As a result, every test that loads the real 1.31 rules XML fails with
`IllegalArgumentException ... (Not a directory)`. That was 20 Server tests here. `ClientTestData` (language and
graphics XML) uses the same pattern, so it is fixed the same way. I didn't check how many Client tests failed beforehand.

The fix resolves from `getParentFile ()` with one fewer `../`. That gives the same folder on Windows, and it now
also works elsewhere. With the fix, all Server (475) and Client (251) tests pass on Linux.
