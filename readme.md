# ACME® Corporation Stores

## TODO
- List stores ✅
  - paginate list of stores ✅
  - fetch data from APIs ✅
- View store details ✅
  - show basic info ✅
  - view extra fields ✅
  - view seasons ✅
- Search stores by name ✅
- Edit store name ✅
- ✨ Download CSV ✅
- ✨ Cron job for worker

## Usage

### Run the tests

Run `./test.sh` so that the whole suite of tests is run.

### Run the app

Run `docker-compose run` at the root to start the webapp, backend and worker to get stores' data.

# Design decisions

## Principles

- Lean approach: I tried to do the minimum needed to deliver user value and reduce over-engineering:
  the least libraries, the minimum assumptions, the least amount of code, a small set of technologies.
- TDD: I followed test-driven development most of the time: in fact, that's my documentation. I don't believe in
  documentation through code comments.
- Clean architecture: I followed the principles of the clean architecture: a core domain and segregated adapters.
- Full-stack: I don't like doing frontend separated from backend. The only way to deliver quick user value is to deliver
  functionality.

### Design decisions

- I didn't create a separated frontend due its simplicity. Currently, the user just needs a page with minimal
  functionality. Therefore, the solution was to serve a static website through the backend app.
- The seasons have functionality other that displaying, which means it would be over-engineering to create tables, do
  joins, creating the code to deal with it, etc. Also, I'd have to make assumptions on how it was to be used (if used at
  all).
