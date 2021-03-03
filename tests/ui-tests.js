import {Selector} from 'testcafe'

fixture`Main task`
    .page`http://localhost:8080`

test('Edit store name', async t => {
    // TODO use testing library instead
    // TODO run in a headless browser
    await t.click(Selector('td').withText('Store'))

    await t.click('[aria-label="Edit store name"]')

    await t.click('input[aria-label="Edit store name"]')
        .pressKey('ctrl+a delete')
        .typeText('input[aria-label="Edit store name"]', 'New store name')

    await t.click(Selector('button').withText('Save'))
        .expect(Selector('tbody tr td').nth(0).textContent)
        .eql('New store name')
})