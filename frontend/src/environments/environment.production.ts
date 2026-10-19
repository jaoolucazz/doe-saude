// O frontend vai ficar em https://tr4tech.com/doesaude/ (ver baseHref em angular.json).
// O backend (API Spring Boot) fica mais simples publicado num subdomínio próprio no
// EasyPanel (ex: api-doesaude.tr4tech.com), em vez de dividir caminho com o frontend
// no mesmo domínio. Troque a URL abaixo pelo endereço real assim que criar o app da API.
export const environment = {
  production: true,
  apiUrl: 'https://api-doesaude.tr4tech.com/api',
  arquivosUrl: 'https://api-doesaude.tr4tech.com'
};
