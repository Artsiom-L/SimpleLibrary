var baseUrl = $('#baseUrl').attr('content');

function leaveComment(bookId) {
    $('#comment-link-' + bookId).removeClass('show');
    $('#comment-link-' + bookId).addClass('hidden');
    $('#delete-' + bookId).removeClass('show');
    $('#delete-' + bookId).addClass('hidden');

    $('#comment-' + bookId).removeClass('hidden');
    $('#comment-' + bookId).addClass('show');
}

function sendComment(bookId) {
    var text = $('#comment-' + bookId + ' > textarea').val();
    $.ajax({
        url: baseUrl + 'comment/add',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            bookId: bookId,
            text: text
        })
    }).done(function () {
        location.reload();
    });
}

function cancelComment(bookId) {
    $('#comment-link-' + bookId).removeClass('hidden');
    $('#comment-link-' + bookId).addClass('show');

    $('#comment-' + bookId).removeClass('show');
    $('#comment-' + bookId).addClass('hidden');

    $('#delete-' + bookId).removeClass('hidden');
    $('#delete-' + bookId).addClass('show');
}

$(document).ready(function () {
    changePageAndSize();
});

function changePageAndSize() {
    $('#pageSizeSelect').change(function (evt) {
        window.location.replace("/?pageSize=" + this.value + "&page=1");
    });
}



