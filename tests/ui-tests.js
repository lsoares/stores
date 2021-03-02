import {screen} from '@testing-library/testcafe'

fixture`Main task`
    .page`http://localhost:8080`

test('Edit store name', async t => {
    await t.click(screen.queryAllByText('Store', {exact: false})[0])
})