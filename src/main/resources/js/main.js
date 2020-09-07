function activateListeners() {
    $('#outer-list li').on('mouseover mouseout', function(e) {
        $(this).children('.button').toggleClass('hovering', e.type === 'mouseover');
        e.stopPropagation();
    });

    $('.button-create').on('click', function() {
        var name = prompt('Please enter a name:');
        
        if (!name) {
            alert('The name cannot be empty');
            return;
        }
        
        sendAjax('page.create?url=' + $(this).siblings('a').attr('href') + '/' + name, function(result, status, xhr) {
            alert("Result: " + result + "\nStatus: " + status);
        });
    });

    $('.button-delete').on('click', function() {
        if (confirm('Are you sure you want to delete this page? This action cannot be undone!')) {
            sendAjax('page.delete?url=' + $(this).siblings('a').attr('href'), function(result, status, xhr) {
                alert("Result: " + result + "\nStatus: " + status);
            });
        }
    });
}

function sendAjax(ajaxQuery, handleResponseFunction) {
    $.ajax({
        url: "/api/" + ajaxQuery,
        type: "GET",
        contentType: false,
        cache: false,
        success: function(result, status, xhr) {
            handleResponseFunction(result, status, xhr);
        },
        error: function(xhr, status, error) {
            var obj = JSON.parse(xhr.responseText);
            alert(obj.error);
        }
    });
}
