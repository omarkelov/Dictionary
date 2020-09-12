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
        
        sendJsonWithAjax('page.create', {path: $(this).siblings('a').attr('href') + '/' + name}, function(result, status, xhr) {
//            alert("Result: " + result + "\nStatus: " + status);
        });
    });

    $('.button-delete').on('click', function() {
        if (confirm('Are you sure you want to delete this page? This action cannot be undone!')) {
            sendJsonWithAjax('page.delete', {path: $(this).siblings('a').attr('href')}, function(result, status, xhr) {
//                alert("Result: " + result + "\nStatus: " + status);
            });
        }
    });
}

function activateGenericListeners() {
    var textField = $('#text-field');
    
    textField[0].onpaste = function(e) {
        // Stop data actually being pasted
        e.stopPropagation();
        e.preventDefault();

        // Get pasted data via clipboard API
        var clipboardData = e.clipboardData || window.clipboardData;
        var text = clipboardData.getData('Text');

        // correct text
        text = text.replace(/\d+\n\d+:\d+:\d+,\d+ --> \d+:\d+:\d+,\d+/g, '')
                   .replace(/\s+/g, ' ')
                   .replace(/(^\s|\s$)/g, '');
        
        textField.val(textField.val() + (textField.val() ? ' ' : '') + capitalize(text));
    }
    
    textField.on('keypress', function(e) {
        if (e.which == 13) {
            $('#phrase-details').empty();
            
            var text = $('#text-field').val();
            var phrases = text.match(/\{.+?\}/g);
            
            var partsOfSpeech = ['Noun', 'Pronoun', 'Verb', 'Phrasal Verb', 'Adjective', 'Adverb', 'Preposition', 'Conjunction', 'Interjection', 'Idiom', 'phrase'];

            $(phrases).each(function() {
                phrase = this.replace(/(\{\s*|\s*\})/g, '');

                var phraseInput = $('<input>').attr('name', 'phrase').val(phrase);
                var correctedPhraseInput = $('<input>').attr('name', 'corrected-phrase').val(phrase);
                var typeSelect = $('<select>').attr('name', 'type');
                $(partsOfSpeech).each(function() {
                    typeSelect.append($('<option>').val(this).text(this));
                });
                var translationInput = $('<input>').attr('name', 'translation');
                
                $('<div>')
                    .addClass('phrase-shell')
                    .append(phraseInput)
                    .append(correctedPhraseInput)
                    .append(typeSelect)
                    .append(translationInput)
                    .appendTo('#phrase-details');
            });
            
            return false;
        }
    });
    
    $('#submit').on('click', function() {
        var phraseData = {
            path: document.location.pathname,
            text: $('#text-field').val().replace(/(\{|\})/g, ''),
            phrases: []
        };
        
        $('.phrase-shell').each(function() {
            phraseData.phrases.push({
                phrase: $(this).find('[name="phrase"]').val(),
                correctedPhrase: $(this).find('[name="corrected-phrase"]').val(),
                type: $(this).find('[name="type"]').val(),
                translation: $(this).find('[name="translation"]').val()
            });
        });
        
        sendJsonWithAjax('phrases.add', phraseData, function(result, status, xhr) {
            
        });
    });
}

function sendJsonWithAjax(method, object, handleResponseFunction) {
    $.ajax({
        url: '/api/' + method + '/' + encodeURIComponent(JSON.stringify(object)),
        type: 'POST',
        contentType: false,
        cache: false,
        success: function(result, status, xhr) {
            handleResponseFunction(result, status, xhr);
        },
        error: function(xhr, status, error) {
            var message;
            
            try {
                var obj = JSON.parse(xhr.responseText);
                message = obj.error;
            } catch (e) {
                message = 'Unexpected error';
            }
            
            alert(message);
        }
    });
}

function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}
