var context = $evaluation.getContext();
var identity = context.getIdentity();
var attributes = identity.getAttributes();
var email = attributes.getValue('email').asString(0);

if (email.endsWith('@redhat.com')) {
    $evaluation.grant();
}